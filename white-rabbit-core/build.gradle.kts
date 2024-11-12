plugins {
  `java-library`
}

dependencies {
  api("org.springframework.boot:spring-boot-starter-validation")
  api("org.springframework.boot:spring-boot-starter-json")
  api("org.springframework.boot:spring-boot-starter-actuator")
  api("org.springframework:spring-tx")

  compileOnly("org.projectlombok:lombok")

  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
  annotationProcessor("org.projectlombok:lombok")
}
