package userProfile.controller.dummy;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import userProfile.service.ConcurrentDummyUserProfile;

@RestController
@RequestMapping("/test/dummy")
@RequiredArgsConstructor
public class DummyUserController {

    private final ConcurrentDummyUserProfile concurrentDummyUserProfile;
    @GetMapping()
    public void dummy() throws InterruptedException {

        concurrentDummyUserProfile.createThousandUsersConcurrently();
    }
}
