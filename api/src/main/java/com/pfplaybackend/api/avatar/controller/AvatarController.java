package com.pfplaybackend.api.avatar.controller;

import com.pfplaybackend.api.avatar.presentation.response.AvatarBodyResponse;
import com.pfplaybackend.api.avatar.service.AvatarService;
import com.pfplaybackend.api.common.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/avatar")
@RestController
public class AvatarController { // 아바타를 전체적으로 관리할거니까 네이밍은 아바타 컨트롤러가 좋을 거 같아요!

    private AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    /**
     * 스프링부트에서는 DI 방식이 3가지가 있는데 (필드, 세터, 생성자) @Autowired는 순환 참조 문제로 사용하지 않고 (테스트 코드 작성할 때 간단히 주입받을 때는 사용)
     * 주로 생성자를 사용합니다.
     * 생성자 주입으로 2가지 방법이 있습니다.
     *
     * 1번 방법 어노테이션을 통해 @RequiredArgsConstructor 이용
     * @RequiredArgsConstructor
     * @RestController
     * public class .... {
     *      private final AvatarService avatarService;  // 해당어노테이션을 사용할 때는 꼭 final로 해야 부트에서 실행 시 주입됩니다.
     * }
     *
     *
     * 2번 방법
     * @RestController
     * public class AvatarListController {
     *     private AvatarService avatarService;
     *
     *     public AvatarListController(AvatarService avatarService) {
     *         this->avatarService = avatarService;
     *     }
     * }
     *
     */

    /**
     * 1. 아바타의 기능이 여러가지 있으니 bodylist를 가져온다는 매핑으로 수정하면 좋을 거 같아요.
     * 2. 기본적으로 컨트롤러는 http 상태값을 표현할 수 있는 객체를 제공하는데요. 이때 제공되는데 ResponseEntity<?> 객체 입니다.
     * 3. 클라이언트에게 기본적으로 전달할 때 아래와 같은 폼을 만들었어요. 그래서 이거 기반으로 추가해주시면 됩니다.
     * 나중에는 ResponseEntity<List<...>> 이렇게 될 수도 있어요! 상황에 따라 좀 다를 수 있습니다.
     * {
     *     "data": {
     *        ....
     *     }
     * }
     * 4. 그리고 jpa에서는 엔티티를 직접 반환하지 않아요! 주로 dto를 통해서 조회해온 객체를 dto / 혹은 response를 담아서 컨트롤러에서 내보내요.
     * 연관관계 때문에 시리얼라이즈 할 때 문제가 될 수 있어서요!
     *
     * 5. 아바타 엔티티도 아바타를 다 관리하는 건 아니기 때문에 아바타 바디나 그런 네이밍으로 수정이 필요할 거 같은데 의견주세요!
     * 6. jpa 사용 시 public or protected 기본 생성자가 있어야합니다! 어노테이션을 사용할 수 있고 직접 생성자를 작성해도 됩니다.
     * 7. /api/v1/avatar를 시큐리티에서 오픈으로 접근 가능하게 하셨는데 클라이언트에서 인증 후 jwt 가지고 해당 api 호출할테니까
     * 오픈으로 안열어놔도 될 거 같아요!
     *
     */
    @GetMapping("/body-list")
    public ResponseEntity<?> getAllAvatarBodys() {
        AvatarBodyResponse avatarBodyResponse = new AvatarBodyResponse(this.avatarService.getAvatarBodys());
        return ResponseEntity.ok(ApiResponse.success(avatarBodyResponse));
    }
}
