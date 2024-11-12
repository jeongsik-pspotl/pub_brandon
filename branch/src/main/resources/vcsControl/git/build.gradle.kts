apply <whybrid.gradle.plugin.WHybridPlugin>()

group = "inswave.whive.vcs"
version = "1.0-SNAPSHOT"

buildscript {

    repositories {
        flatDir{
            dirs("../libs")
        }
        mavenCentral()
    }

    dependencies {
        classpath("wmatrix-gradle-plugin:wmatrix-gradle-plugin-2.0.4")
        classpath("org.eclipse.jgit:org.eclipse.jgit:5.9.0.202009080501-r")
        classpath ("com.google.code.gson:gson:2.8.6")
        classpath ("org.codehaus.groovy:groovy-all:2.4.15")
        classpath ("org.codehaus.groovy:groovy-xml:2.0.1")
        classpath (gradleApi())
        classpath ("org.json:json:20180813")
        classpath ("pl.droidsonroids.yaml:snakeyaml:1.18-android")
    }

    tasks{
        val clone by registering {
            group = "vcsControl"
            dependsOn("gitClone")

            doLast {
                println("git Clone Finished")
            }
        }

        val pull by registering {
            group = "vcsControl"
            dependsOn("gitPull")

            doLast {
                println("git Pull Finished")
            }
        }

        val push by registering {
            group = "vcsControl"
            dependsOn("gitPush")

            doLast {
                println("git Push Finished")
            }
        }

        val addAll by registering {
            group = "vcsControl"
            dependsOn("gitAddAll")

            doLast {
                println("git AddAll Finished")
            }
        }

        val add by registering {
            group = "vcsControl"
            dependsOn("gitAdd")

            doLast {
                println("git Add Finished")
            }
        }

        val commitAll by registering {
            group = "vcsControl"
            dependsOn("gitCommitAll")

            doLast {
                println("git CommitAll Finished")
            }
        }

        val commit by registering {
            group = "vcsControl"
            dependsOn("gitCommit")

            doLast {
                println("git Commit Finished")
            }
        }
    }
}