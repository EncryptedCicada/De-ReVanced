dependencies {
    compileOnly(project(":extensions:shared:library"))
    compileOnly(project(":extensions:instagram:stub"))
    compileOnly(libs.annotation)
}

extension {
    name = "extensions/instagram.mpe"
}