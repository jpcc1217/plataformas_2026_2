from playwright.sync_api import sync_playwright
import time
import config
from paginas.login import PaginaLogin


def main() -> None:
    with sync_playwright() as p:
        navegador = p.chromium.launch(headless=config.HEADLESS, slow_mo=500)
        pagina = navegador.new_page()

        PaginaLogin(pagina, config.PORTAL_URL).login(config.PORTAL_USUARIO, config.PORTAL_CLAVE)


        navegador.close()


if __name__ == "__main__":
    main()