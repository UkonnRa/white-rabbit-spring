rootProject.name = "white-rabbit"

file(".").listFiles { it ->
  it.isDirectory && it.list()?.contains("build.gradle.kts") ?: false
}?.forEach {
  include(it.name)
}
