package com.sudox.android.common.di.module.fragments

import com.sudox.android.ui.main.contacts.ContactsFragment
import com.sudox.android.ui.main.contacts.add.ContactAddFragment
import com.sudox.android.ui.main.messages.MessagesFragment
import com.sudox.android.ui.main.profile.ProfileFragment
import com.sudox.android.ui.main.settings.SettingsFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MainActivityModule {

    @ContributesAndroidInjector
    abstract fun provideContactsFragment(): ContactsFragment

    @ContributesAndroidInjector(modules = [MessagesFragmentModule::class])
    abstract fun provideMessagesFragment(): MessagesFragment

    @ContributesAndroidInjector(modules = [ProfileFragmentModule::class])
    abstract fun provideProfileFragment(): ProfileFragment

    @ContributesAndroidInjector(modules = [ContactAddFragmentModule::class])
    abstract fun provideContactAddFragment(): ContactAddFragment

    @ContributesAndroidInjector
    abstract fun provideSettingsFragment(): SettingsFragment
}