package com.sudox.android.ui.messages

import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import com.sudox.android.R
import com.sudox.android.data.models.chats.ChatType
import com.sudox.android.ui.messages.chat.ChatFragment
import dagger.android.support.DaggerAppCompatActivity
import javax.inject.Inject

class MessagesInnerActivity : DaggerAppCompatActivity() {

    companion object {
        const val CONVERSATION_TYPE_KEY = "chat_type_key"
        const val CONVERSATION_RECIPIENT_KEY = "chat_recipient_key"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messages_inner)

        // Get type of the chat
        val chatTypeOrdinal = intent.getIntExtra(CONVERSATION_TYPE_KEY, -1)

        // Защита от дурака!
        if (chatTypeOrdinal != -1) {
            when (ChatType.values()[chatTypeOrdinal]) {
                ChatType.CHAT -> showUserChatFragment()
            }
        } else finish()
    }

    private fun showUserChatFragment() {
        supportFragmentManager.beginTransaction()
//                .setCustomAnimations(R.animator.animator_fragment_change, 0)
                .replace(R.id.fragmentChatContainer, ChatFragment().apply { arguments = intent.extras })
                .commit()
    }
}