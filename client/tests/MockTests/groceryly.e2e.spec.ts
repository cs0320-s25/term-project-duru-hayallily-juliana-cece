// client/tests/MockTests/map.e2e.spec.ts
import { test, expect } from "@playwright/test";
import { clerk } from "@clerk/testing/playwright";

test.setTimeout(60000);

test.describe("Frontend", () => {
  test.beforeEach(async ({ page }) => {
    await page.goto("http://localhost:5173/");
  });

  test("login visible", async ({ page }) => {
    const signin = page.getByLabel("sign in");
    await expect(signin).toBeVisible();
  });

  test("login success", async ({ page }) => {
    await clerk.signIn({page,
      signInParams: {
        strategy: "password",
        password: "notrealnotreal",
        identifier: "test@brown.edu",
      },});

    const logout = page.getByRole("button", { name: "log out" });

    await expect(logout).toBeVisible();
  });

  test("pantry", async ({ page }) => {
    const signin = page.getByLabel("sign in");
    
    await clerk.signIn({page,
      signInParams: {
        strategy: "password",
        password: "notrealnotreal",
        identifier: "test@brown.edu",
      },});

    const textbox = page.getByLabel("pantry input");
    const addButton = page.getByLabel("pantry add");
    
    await textbox.fill("oatmeal");

    await addButton.click;

    await expect(page.getByText("oatmeal")).toBeVisible();
  });

  test("recipies", async ({ page }) => {
    const signin = page.getByLabel("sign in");
    
    await clerk.signIn({page,
      signInParams: {
        strategy: "password",
        password: "notrealnotreal",
        identifier: "test@brown.edu",
      },});

    await page.goto("http://localhost:5173/meal-plan"); 

    const textbox = page.getByLabel("recipie input");
    const clearButton = page.getByText("clear all");
    
    await textbox.fill("ostrich egg");
    await expect(page.getByText("No recipes found for \"ostrich egg\"")).toBeVisible();

    await textbox.fill("egg");
    await expect(page.getByText("Recipie Results")).toBeVisible();

    await textbox.fill("");
    await clearButton.click();
    await expect(page.getByText("(No meals planned yet!)")).toBeVisible();
  });

  test("grocery list", async ({ page }) => {
    const signin = page.getByLabel("sign in");

    await page.goto("http://localhost:5173/grocery-list");
    
    await clerk.signIn({page,
      signInParams: {
        strategy: "password",
        password: "notrealnotreal",
        identifier: "test@brown.edu",
      },});

    const textbox = page.getByLabel("grocery add");
    const addButton = page.getByText("Add");
    const clearButton = page.getByText("Clear All");

    await expect(page.getByText("(nothing to buy!)")).toBeVisible();
    
    await textbox.fill("oats");
    await addButton.click();
    await expect(page.getByText("oats")).toBeVisible();

    await clearButton.click;

    await expect(page.getByText("(nothing to buy!)")).toBeVisible();
  });
});