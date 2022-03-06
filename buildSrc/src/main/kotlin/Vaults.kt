import com.bkahlert.kommons.shell.ShellScript
import org.gradle.api.Project
import org.gradle.api.Task
import org.yaml.snakeyaml.Yaml

class Vaults(val project: Project) {
    inline operator fun <reified T : Any> get(relative: String, vararg path: String): T {
        val vault = project.projectDir.resolve(relative)
        require(vault.exists()) { "$vault does not exist" }

        val decrypted = ShellScript { "ansible-vault view $vault" }.exec().waitFor().apply {
            check(successful) { "Failed to decrypt $vault: $io" }
        }.io.ansiRemoved

        return Yaml().loadSubTree(decrypted, path.toList())
    }
}

inline fun <reified T : Any> Yaml.loadSubTree(yaml: String, path: List<String>): T {
    val subTreeYaml = if (path.isEmpty()) yaml else {
        val subTree = path.fold(load<Map<*, *>>(yaml)) { acc, segment ->
            acc.get(segment).let { it as? Map<*, *> } ?: throw IllegalArgumentException("$path does not exist")
        }
        dump(subTree)
    }
    val subTree = load<Map<String, Any?>>(subTreeYaml)
    return T::class.instantiate(subTree)
}

val Project.vaults get() = Vaults(this)
val Task.vaults get() = Vaults(project)
