package org.abhijitsarkar;

import lombok.RequiredArgsConstructor;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Abhijit Sarkar
 */
@Controller
@RequiredArgsConstructor
public class FacebookController {
    private final Facebook facebook;

    @ModelAttribute
    public void addUser(Model model) {
        /* http://stackoverflow.com/a/39902266/839733 */
        String[] fields = {"id", "email", "first_name", "last_name"};
        User userProfile = facebook.fetchObject("me", User.class, fields);
        model.addAttribute("user", userProfile);
    }

    @GetMapping({"/", "feed"})
    public String feed(Model model) {
        PagedList<Post> feed = facebook.feedOperations().getFeed();
        model.addAttribute("feed", feed);

        return "feed";
    }
}
