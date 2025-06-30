package com.github.damianjester.nclient.ui.gallery.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.github.damianjester.nclient.ui.theme.NClientPreviewTheme
import kotlin.random.Random

/**
 * Emojis
 * https://asciimoji.com/
 */
enum class AsciiEmoji(val text: String) {
    Koala("ʕ·͡ᴥ·ʔ"),
    DeathStare("(☉_☉)"),
    Afraid("(ㆆ _ ㆆ)"),
    Ass("(‿|‿)"),
    Blubby("( 0 _ 0 )"),
    Bored("(-_-)"),
    Cry("(╥﹏╥)"),
    Depressed("(︶︹︶)"),
    Derp("☉ ‿ ⚆"),
    Help("\\(°Ω°)/"),
    LennyShrug("¯\\_( ͡° ͜ʖ ͡°)_/¯"),
    SadLenny("( ͡° ʖ̯ ͡°)"),
    Shrug("¯\\_(ツ)_/¯"),
    Meep("\\(°^°)/"),
    Meh("ಠ_ಠ"),
    Wut("⊙ω⊙")
}

@Composable
fun AsciiEmojiMessage(
    text: String,
    emoji: AsciiEmoji,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            emoji.text,
            modifier = Modifier.clearAndSetSemantics {},
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun AsciiEmojiMessage(
    text: String,
    modifier: Modifier = Modifier,
) {
    val emoji = remember { AsciiEmoji.entries[Random.nextInt(0, AsciiEmoji.entries.size)] }
    AsciiEmojiMessage(
        text = text,
        emoji = emoji,
        modifier = modifier
    )
}

private class AsciiEmojiParameterProvider : PreviewParameterProvider<AsciiEmoji> {
    override val values: Sequence<AsciiEmoji> = AsciiEmoji.entries.asSequence()
}

@Preview(
    showBackground = true
)
@Composable
private fun AsciiEmojiMessagePreview(
    @PreviewParameter(AsciiEmojiParameterProvider::class) emoji: AsciiEmoji
) {
    NClientPreviewTheme {
        AsciiEmojiMessage(
            text = "No items in list",
            emoji = emoji,
            modifier = Modifier.padding(16.dp)
        )
    }
}
