from playwright.sync_api import sync_playwright
import time
import config


def main() -> None:
    with sync_playwright() as p:
        navegador = p.chromium.launch(headless=config.HEADLESS, slow_mo=500)
        pagina = navegador.new_page()
        pagina.goto(f"{config.PORTAL_URL}/login")
        pagina.locator("#txtUsr").fill(config.PORTAL_USUARIO)
        pagina.locator("#txtPwd").fill(config.PORTAL_CLAVE)
        pagina.locator("#btnLogin").click()
        print(f"Página después del login: {pagina.title()}")
        pagina.screenshot(path=config.CARPETA_SALIDA + "/despues_login.png")
        navegador.close()


if __name__ == "__main__":
    main()