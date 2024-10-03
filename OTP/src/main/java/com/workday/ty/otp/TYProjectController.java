package com.workday.ty.otp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


/**
 * Run http://localhost:8081/login to start
 */
@Controller
public class TYProjectController {

    /**
     * When isSixDigitCodeInUse is set to True verifyCode page will show.
     */
    private final boolean isSixDigitCodeInUse = false;

    private TYTeamDetails teamDetails = new TYTeamDetails();

    @GetMapping("/login")
    public String getLogin() {
        return "login";
    }

    /**
     * First Page of Web Application - login page - Asks for Username and Password
     */
    @PostMapping("/login")
    public String postLogin(@ModelAttribute(name="loginForm") TYTeamDetails login, Model model) {
        teamDetails =  TYProjectFileUtils.checkUsernamePassword(login.getUsername(), login.getPassword());
        if (teamDetails == null) {
            model.addAttribute("error", "Incorrect Username & Password");
            return "login";
        } else {
            if (isSixDigitCodeInUse) {
                // Generate OTPC
                String code = TYProjectEmailUtils.generateOneTimeCodeAndMessage();
                // Save code to file with correct usename and email for verification later
                TYProjectFileUtils.addSixDigitCodeToFile(teamDetails.getUsername(), teamDetails.getEmail(), code);
                // Send email with code
                new TYProjectEmailUtils().sendEmail(teamDetails.getUsername(), teamDetails.getEmail(), code);
                return "verifycode";
            } else {
                return "tooldesign";
            }
        }
    }

    /**
     * Second Page of Web Application - Verify Code - Enter your 6-digit code here
     */
    @PostMapping("/verifycode")
    public String postVerifyCode(@ModelAttribute(name="code") String code, Model model) {

        if (code != null && code.length() != 6) {
            model.addAttribute("error", "Code entered is not six digits!");
        }
        boolean result = TYProjectFileUtils.validateCode(teamDetails.getUsername(), teamDetails.getEmail(), code);
        if (!result) {
            model.addAttribute("error", "Incorrect code");
            return "verifycode";
        } else {
            return "tooldesign";
        }
    }

    /**
     * Third page of Web Application - Design Page -  this is where you will add HTML
     */
    @GetMapping("/tooldesign")
    public String getToolDesign() {
        return "tooldesign";
    }


    @RequestMapping(value = "/examples", method = RequestMethod.GET)
    public String getExamples() {
        return "examples/index";
    }

    @RequestMapping(value = "examples/typography", method = RequestMethod.GET)
    public String getTypography() {
        return "examples/typography";
    }

    @RequestMapping(value = "examples/forms", method = RequestMethod.GET)
    public String getForms() {
        return "examples/forms";
    }

    @RequestMapping(value = "examples/images", method = RequestMethod.GET)
    public String getImages() {
        return "examples/images";
    }

    @RequestMapping(value = "examples/fonts", method = RequestMethod.GET)
    public String getFonts() {
        return "examples/fonts";
    }
}