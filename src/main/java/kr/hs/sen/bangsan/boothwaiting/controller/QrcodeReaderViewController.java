package kr.hs.sen.bangsan.boothwaiting.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class QrcodeReaderViewController {

    @GetMapping(path = "/qr-reader")
    public String barcode() {
        return "qrcodeReader";
    }
}
