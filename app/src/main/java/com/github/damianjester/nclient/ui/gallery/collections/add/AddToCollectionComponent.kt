package com.github.damianjester.nclient.ui.gallery.collections.add

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.active
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.MutableValue
import com.arkivanov.decompose.value.Value
import com.github.damianjester.nclient.core.models.GalleryId
import com.github.damianjester.nclient.ui.gallery.collections.add.AddToCollectionComponent.Child
import com.github.damianjester.nclient.ui.gallery.collections.add.create.CreateCollectionComponent
import com.github.damianjester.nclient.ui.gallery.collections.add.create.DefaultCreateCollectionComponent
import com.github.damianjester.nclient.ui.gallery.collections.add.selection.CollectionSelectionComponent
import com.github.damianjester.nclient.ui.gallery.collections.add.selection.DefaultCollectionSelectionComponent
import com.github.damianjester.nclient.ui.utils.asStateFlow
import com.github.damianjester.nclient.utils.NClientDispatchers
import com.github.damianjester.nclient.utils.coroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

interface AddToCollectionComponent {
    val stack: Value<ChildStack<*, Child>>

    val submittable: Value<Boolean>

    fun submit()

    fun navigateBack()

    sealed interface Child {
        val submittable: Submittable

        class CollectionSelection(val component: CollectionSelectionComponent) : Child {
            override val submittable = component
        }

        class CreateCollection(val component: CreateCollectionComponent) : Child {
            override val submittable = component
        }

        interface Submittable {
            val submittable: Flow<Boolean>

            fun submit()
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultAddToCollectionComponent(
    context: ComponentContext,
    galleryId: GalleryId,
    private val onDismissRequest: () -> Unit,
    dispatchers: NClientDispatchers,
) : AddToCollectionComponent, KoinComponent, ComponentContext by context {
    private val coroutineScope = coroutineScope(dispatchers.Main.immediate + SupervisorJob())

    private val navigation = StackNavigation<Config>()

    override val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            serializer = Config.serializer(),
            initialConfiguration = Config.CollectionSelection(galleryId),
            handleBackButton = true,
            childFactory = ::child,
        )

    private val _submittable = MutableValue(false)
    override val submittable: Value<Boolean> = _submittable

    init {
        coroutineScope.launch {
            stack.asStateFlow()
                .flatMapLatest { it.active.instance.submittable.submittable }
                .collectLatest {
                    _submittable.value = it
                }
        }
    }

    private fun child(config: Config, context: ComponentContext): Child =
        when (config) {
            is Config.CollectionSelection -> Child.CollectionSelection(collectionSelectionComponent(config, context))
            Config.CreateCollection -> Child.CreateCollection(createCollection(context))
        }

    private fun collectionSelectionComponent(
        config: Config.CollectionSelection,
        componentContext: ComponentContext,
    ) = DefaultCollectionSelectionComponent(
        componentContext = componentContext,
        config = config,
        onNavigateCreateCollection = { navigation.pushNew(Config.CreateCollection) },
        onSubmitted = onDismissRequest,
        dispatchers = get(),
        repository = get(),
        selectionUpdater = get()
    )

    private fun createCollection(context: ComponentContext) =
        DefaultCreateCollectionComponent(
            componentContext = context,
            onNewCollectionCreated = { id ->
                navigation.pop {
                    (stack.active.instance as? Child.CollectionSelection)?.submittable
                        ?.onCollectionCreated(id = id)
                }
            },
            dispatchers = get(),
            collectionCreator = get(),
        )

    override fun submit() {
        stack.active.instance.submittable.submit()
    }

    override fun navigateBack() {
        if (stack.value.backStack.isNotEmpty()) {
            navigation.pop()
        } else {
            onDismissRequest()
        }
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data class CollectionSelection(val id: GalleryId) : Config

        @Serializable
        data object CreateCollection : Config
    }
}
