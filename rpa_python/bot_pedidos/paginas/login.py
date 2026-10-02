from playwright.sync_api import Page
import time
import config


class PaginaLogin:
    def __init__(self, pagina: Page, url_portal: str) -> None:
        self.pagina = pagina
        self.url_portal = f"{url_portal}/login"

    def login(self, usuario: str, clave: str) -> None:
        self.pagina.goto(self.url_portal)
        self.pagina.locator("#txtUsr").fill(usuario)
        self.pagina.locator("#txtPwd").fill(clave)
        self.pagina.locator("#btnLogin").click()


        print(f"Página después del login: {self.pagina.title()}")
        self.pagina.screenshot(path=config.CARPETA_SALIDA + "/despues_login.png")