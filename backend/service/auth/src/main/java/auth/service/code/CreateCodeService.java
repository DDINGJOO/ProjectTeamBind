package auth.service.code;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateCodeService {

    public String  createCode(Long userid) {
        long index =  System.currentTimeMillis()%12;

        String strUserId = String.valueOf(userid);
        return strUserId.substring((int)index, (int)index+4);

    }
}
