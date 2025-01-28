package info.dmerej;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AddTeamTest {
    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    @BeforeEach
    public void setup(){
        // Use playwright driver to get a browser and open a new page
        playwright = Playwright.create();
        var launchOptions = new BrowserType.LaunchOptions().setHeadless(false)
                .setSlowMo(1000); // Remove this when you're done debugging
        browser = playwright.firefox().launch(launchOptions);

        // Set base URL for the new context
        var contextOptions = new Browser.NewContextOptions();
        contextOptions.setBaseURL("https://a.se1.hr.dmerej.info");
        context = browser.newContext(contextOptions);

        page = context.newPage();
    }
    @Test
    void test_add_team() {
        // Reset database
        page.navigate("/reset_db");
        var proceedButton = page.locator("button:has-text('proceed')");
        proceedButton.click();
        page.navigate("/");

        // Add a new team
        page.navigate("/add_team");
        var nameInput = page.locator("input[name=\"name\"]");
        var teamName = "my team";
        nameInput.fill(teamName);
        page.click("text='Add'");

        // Check that the team has been added
        page.navigate("/teams");

        // Check the new team is there
        String selector = String.format("td:has-text('%s')", teamName);
        var isVisible = page.isVisible(selector);
        assertTrue(isVisible);
    }
    @Test
    void test_add_team_empty_team() {
        page.navigate("/add_team");
        var nameInput = page.locator("input[name=\"name\"]");
        var teamName = " ";
        nameInput.fill(teamName);
        page.click("text='Add'");

        var result ="Server Error (500)";
        String selector = String.format("h1:has-text('%s')", result);
        boolean isErrorVisible = page.isVisible(selector);

        assertFalse(isErrorVisible, "The error message 'Server Error (500)' should not be visible.");
    }
}
