plugins {
  id("org.springframework.boot")
  id("org.graalvm.buildtools.native")

  id("com.google.protobuf") version "0.9.4"
}

object Version {
  const val GRPC_STRING = "3.1.0.RELEASE"
  const val PROTOC = "4.28.3"
  const val PROTOBUF = "4.28.3"
  const val GRPC = "1.68.1"
  const val JAKARTA_ANNOTATION = "1.3.5"
}

dependencies {
  implementation(platform("io.grpc:grpc-bom:${Version.GRPC}"))
  implementation(platform("com.google.protobuf:protobuf-bom:${Version.PROTOBUF}"))

  implementation(project(":white-rabbit-core"))
  implementation("net.devh:grpc-server-spring-boot-starter:${Version.GRPC_STRING}")
  implementation("org.springframework.boot:spring-boot-starter-security")

  compileOnly("jakarta.annotation:jakarta.annotation-api:${Version.JAKARTA_ANNOTATION}")

  runtimeOnly("com.h2database:h2")

  testImplementation("net.devh:grpc-client-spring-boot-starter:${Version.GRPC_STRING}")
}

protobuf {
  protoc { artifact = "com.google.protobuf:protoc:${Version.PROTOC}" }
  plugins {
    create("grpc") {
      artifact = "io.grpc:protoc-gen-grpc-java:${Version.GRPC}"
    }
  }
  generateProtoTasks {
    all().forEach {
      it.plugins {
        create("grpc")
      }
    }
  }
}

graalvmNative {
  binaries.all {
    // https://build-native-java-apps.cc/framework/samples/spring-native/grpc/
    buildArgs.addAll(
      "--initialize-at-run-time=io.grpc.netty.shaded.io.netty",
    )
  }
}
