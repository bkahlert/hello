import kotlin.reflect.KClass
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.jvmErasure

inline fun <reified T : Any> KClass<T>.instantiate(data: Map<String, Any?>): T {
    return instantiateUsingMatchingParametersOrNull(data)
        ?: instantiateUsingMatchingMapParameterOrNull(data)
        ?: throw InstantiationException("No matching ${T::class} constructor for $data")
}

inline fun <reified T : Any> KClass<T>.instantiateUsingMatchingParametersOrNull(data: Map<String, Any?>): T? {
    val types = data.mapValues { (_, value) -> value?.let { it::class.starProjectedType } }
    return constructors.firstOrNull {
        it.parameters.all { param ->
            types.entries.firstOrNull { (key, type) ->
                val matchingKey = param.name == key
                val matchingType = type?.run { param.type.isSupertypeOf(this) } ?: param.type.isMarkedNullable
                matchingKey && matchingType
            } != null
        }
    }?.let {
        it.callBy(it.parameters.associateWith { param -> data[param.name] })
    }
}

inline fun <reified T : Any> KClass<T>.instantiateUsingMatchingMapParameterOrNull(data: Map<String, Any?>): T? = constructors.firstOrNull {
    it.parameters.singleOrNull { param ->
        param.type.jvmErasure.isInstance(data)
    } != null
}?.call(data.toSortedMap())
