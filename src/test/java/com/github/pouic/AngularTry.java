package com.github.pouic;


import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * Created with IntelliJ IDEA.
 * User: pouic
 * Date: 28/11/13
 * Time: 20:27
 * To change this template use File | Settings | File Templates.
 */
public class AngularTry {

    private HttpServer server;

    @Before
    public void init() throws IOException {
        server = HttpServer.create(new InetSocketAddress(8000), 0);
        server.createContext("/test", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    @After
    public void close(){
        server.stop(0);
    }


    @Test
    public void jsTest(){
        WebDriver driver = new HtmlUnitDriver();
        ((HtmlUnitDriver)driver).setJavascriptEnabled(true);

        driver.get("http://localhost:8000/test");

        WebElement div = ((HtmlUnitDriver) driver).findElementById("demo");
        Assert.assertFalse(div.getText().contains("yop"));
        ((HtmlUnitDriver) driver).executeScript("window.sout();");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        Assert.assertTrue(div.getText().contains("yop"));

        WebElement divFromJs = (WebElement) ((HtmlUnitDriver) driver).executeScript("return document.getElementById('demo');");
        Assert.assertTrue(divFromJs.getText().contains("yop"));
    }



    private class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            BufferedReader br = null;
            StringBuilder sb = new StringBuilder();
            String line;
            try {

                br = new BufferedReader(new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("angular.html")));
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (br != null) {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            String response = sb.toString();
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
