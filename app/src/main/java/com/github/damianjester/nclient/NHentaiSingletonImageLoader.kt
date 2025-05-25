package com.github.damianjester.nclient

import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.network.NetworkFetcher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class NHentaiSingletonImageLoader : SingletonImageLoader.Factory, KoinComponent {

    private val factory by inject<NetworkFetcher.Factory>()

    override fun newImageLoader(context: PlatformContext): ImageLoader {
        return ImageLoader.Builder(context)
            .components {
                add(factory)
            }
            .build()
    }
}
