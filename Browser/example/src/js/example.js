import { Browser } from '@capacitor&#x2F;browser-extended';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    Browser.echo({ value: inputValue })
}
