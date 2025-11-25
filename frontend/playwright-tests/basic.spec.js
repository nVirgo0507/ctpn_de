const { test, expect } = require('@playwright/test');

const BASE_URL = 'http://localhost:3000';

test.describe('Basic Web App Functionality', () => {
  test('Homepage loads and shows login link', async ({ page }) => {
    await page.goto(BASE_URL);
    await expect(page).toHaveTitle(/chungtayphongngua/i);
    await expect(page.locator('text=Đăng nhập')).toBeVisible();
  });

  test('Login with valid credentials', async ({ page }) => {
    await page.goto(`${BASE_URL}/login`);
    await page.fill('input[name="email"]', 'hardcoded@example.com');
    await page.fill('input[name="password"]', '123456');
    await page.click('button[type="submit"]');
    // Wait for redirect or dashboard
    await expect(page.locator('text=Đăng xuất')).toBeVisible();
  });

  test('Access a protected page after login', async ({ page }) => {
    await page.goto(`${BASE_URL}/login`);
    await page.fill('input[name="email"]', 'hardcoded@example.com');
    await page.fill('input[name="password"]', '123456');
    await page.click('button[type="submit"]');
    // Wait for login
    await expect(page.locator('text=Đăng xuất')).toBeVisible();
    // Try to access a protected route (adjust as needed)
    await page.goto(`${BASE_URL}/courses/my-courses`);
    await expect(page.locator('text=Khóa học của tôi')).toBeVisible();
  });

  test('Logout works', async ({ page }) => {
    await page.goto(`${BASE_URL}/login`);
    await page.fill('input[name="email"]', 'hardcoded@example.com');
    await page.fill('input[name="password"]', '123456');
    await page.click('button[type="submit"]');
    await expect(page.locator('text=Đăng xuất')).toBeVisible();
    await page.click('text=Đăng xuất');
    await expect(page.locator('text=Đăng nhập')).toBeVisible();
  });
}); 