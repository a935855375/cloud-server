package com.example.demo.repostory

import com.example.demo.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
trait UserRepository extends JpaRepository[User, Int]
