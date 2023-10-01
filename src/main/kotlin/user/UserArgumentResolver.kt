package com.wafflestudio.seminar.spring2023.user

import com.wafflestudio.seminar.spring2023.user.service.User
import com.wafflestudio.seminar.spring2023.user.service.UserService
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/* TODO: 추가과제
HandlerMethodArgumentResolver는 메소드 파라미터들을 인자값들에 주입해주는 인터페이스이다
HandlerMethodArgumentResolver 인터페이스에 대해 그 구현체로써 내부의 로직처리를 위해 UserService를 주입받는 UserArgumentResolver를 구현하자.
내부에는 어떤 파라미터들에 대해서 작업을 수행할건지(지원할 건지) 여부를 결정하는 supportsParameter와, 실제로 그 파라미터들을 가지고 어떤 로직으로 처리를 해줄지 결정하는 resolveArgument를 정의해줘야 한다. */


class UserArgumentResolver( //구현체
    private val userService: UserService, //resolveArgument에서의 로직 처리를 위해 주입! 받는다.
) : HandlerMethodArgumentResolver {  //인터페이스

    override fun supportsParameter(parameter: MethodParameter): Boolean { //처리 대상이 되는 파라미터를 지정하기 때문에 boolean 형이다.
        return parameter.parameterType == User::class.java  /*controller method 에 들어오는 parameter 중 User 라는 타입이 있으면 자신이 그것에 대해 지원하는 argumentresolver 라고 알려주는 역할*/
    } 

    override fun resolveArgument(//실제 해당 파라미터들로 처리해야할 로직에 대해서 정의한다. 우리는 응답 토큰의 처리에 대해서 간결하게 만들고 싶은 거니까 그것만 정의하면 된다.
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?, //변수의 유형이 이렇게 되어있는 이유 -> override 라서 구현한 interface 에 해당하는 HandlerMethodArgumentResolver에 정의된 메서드의 signature(형태)대로 일치해야 언어 규약을 지키는 것이기 때문
    ): User {
        val accessToken = webRequest.getHeader("Authorization")?.split(" ")?.get(1) ?: ""
        /*토큰을 만드는 과정을 미리 처리하기 위해 위와 같은 코드를 작성했다.
        webRequest로 url 형식으로 들어오는 요청 헤더에서 키가 "Authorization"인 요청의 밸류만 빼낸뒤, 띄어쓰기를 기준으로 갈라서 두 번째 요소인 진짜 토큰을 빼내는 코드이다.
        만약 그러한 요청이 없거나 쪼갤 것이 없거나 한다면 그냥 빈 문자열을 대입하게 된다.
        */

        return userService.authenticate(accessToken) //이렇게 만든 토큰을 상속받아둔 userService의 authenticate로 리턴하면 토큰에 관한 처리는 끝이다. 나머지는 Controller에서 응답 처리를 해주면 된다
    }
}

@Configuration //구현한 HandlerMethodArgumentResolver를 사용하려면 addArgumentResolver에 등록해줘야 한다. 아래가 그 과정이 되겠다.
class WebConfig(
    private val userService: UserService,
) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(UserArgumentResolver(userService)) //userService에 대한 UserArgumentResolver의 사용을 등록한다.
    }
}
