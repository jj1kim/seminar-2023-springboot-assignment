package com.wafflestudio.seminar.spring2023.user.repository

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, Long>{ fun findByUsername(username:String) : UserEntity? }
//여기서는 UserRespository에 JpaRepository(스프링에서 주어짐)을 "상속" 받은 것이다. 이렇게 하면 UserRepository는 인터페이스를 미리 만들어둔 뒤 구현체와 세부적인 기능 함수를 구현하지 않아도 스프링에서 알아서 함수 이름에 걸맞는 내용과 그 구현체를 짜준다.
