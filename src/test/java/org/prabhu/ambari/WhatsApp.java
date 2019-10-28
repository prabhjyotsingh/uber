package org.prabhu.ambari;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

/**
 * Created by prabhjyot.singh on 01/01/16.
 */
public class WhatsApp extends AbstractIT {

  private static final long MAX_WAIT = 5;

  @Before
  public void startUp() {
    driver = WebDriverManager.getWebDriver("https://web.whatsapp.com/", null);
  }

  @After
  public void shutDown() {
    driver.close();
  }


  @Test
  public void testSendWhatApp() {
    try {

      pollingWait(By.xpath("//header/div[1]/div/img"), MAX_WAIT);

      sleep(2000, false);

      pollingWait(By.xpath("//header/div[2]/div/span/div[2]/div[contains(@role,'button')]"),
          MAX_WAIT)
          .click();

      sleep(2000, false);

      JavascriptExecutor js = ((JavascriptExecutor) driver);

      String contactElementClassName = "rK2ei";

      Long scrollHeight = (Long) js
          .executeScript("return document.getElementsByClassName('" + contactElementClassName
              + "')[0].scrollHeight;");

      Set<String> contactsName = new HashSet();
      Long scrollTo = 0l;
      while (scrollTo < scrollHeight) {
        js.executeScript(""
            + "var element = document.getElementsByClassName('" + contactElementClassName + "')[0];"
            + "element.scrollTo(0, " + scrollTo.toString() + ");");
        sleep(100, false);
        List<WebElement> contacts = driver.findElements(By.xpath(
            "//div[contains(@class,'" + contactElementClassName + "')]/div[2]/div/div/div"));
        for (int i = 1; i < contacts.size(); i++) {
          try {
            if (contacts.get(i).getText().length() > 3) {
              contactsName.add(contacts.get(i)
                  .findElement(By.className("KgevS")).getText());
            }
          } catch (Exception e) {
            // ignore
            System.out.println(e);
          }
        }
        scrollTo += 600;
      }

      driver.findElement(By.xpath("//header//button")).click();

      List<String> notNames = new ArrayList<>();

      for (String contact : contactsName) {
        if (!notNames.contains(contact)) {
          try {
            sleep(1000, false);
            pollingWait(By.xpath("//div[@id='side']//button"), MAX_WAIT)
                .click();

            driver.findElement(By.xpath("//div[@id='side']//input")).sendKeys(contact);
            sleep(500, false);
            for (WebElement ele : driver.findElements(By.xpath("//span[@class='_19RFN _1ovWX']"))) {
              if (ele.getAttribute("title").equals(contact)) {
                ele.click();
                break;
              }
            }

            WebElement textBox = pollingWait(By.xpath("//footer/div/div[2]/div/div[2]"), MAX_WAIT);
            textBox.sendKeys(
                "Wish the divine lights of Diwali bring peace, prosperity, health, and love to your life. Happy Diwali!");
            textBox.sendKeys(Keys.chord(Keys.SHIFT, Keys.ENTER));
            textBox.sendKeys("Regard Kabir, Geet, and Prabh");
            textBox.sendKeys("\n");
          } catch (Exception e) {
            System.out.println("exep for::" + contact);
          }

        } else {
          System.out.println(contact);
        }
      }

    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
