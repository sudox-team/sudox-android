package com.sudox.messenger.android.countries

import com.sudox.messenger.android.countries.vos.CountryVO

val COUNTRIES = hashMapOf(
        createCountryPair(CountryVO("RU", R.string.russia, R.drawable.ic_flag_russia, 7)),
        createCountryPair(CountryVO("CZ", R.string.czech_republic, R.drawable.ic_flag_czech_republic, 420)),
        createCountryPair(CountryVO("DE", R.string.germany, R.drawable.ic_flag_germany, 49)),
        createCountryPair(CountryVO("HU", R.string.hungary, R.drawable.ic_flag_hungary, 36)),
        createCountryPair(CountryVO("IS", R.string.iceland, R.drawable.ic_flag_iceland, 354)),
        createCountryPair(CountryVO("IT", R.string.italy, R.drawable.ic_flag_italy, 39)),
        createCountryPair(CountryVO("LV", R.string.latvia, R.drawable.ic_flag_latvia, 371)),
        createCountryPair(CountryVO("LT", R.string.lithuania, R.drawable.ic_flag_lithuania, 370)),
        createCountryPair(CountryVO("NL", R.string.neatherlands, R.drawable.ic_flag_neatherlands, 31)),
        createCountryPair(CountryVO("PL", R.string.poland, R.drawable.ic_flag_poland, 48)),
        createCountryPair(CountryVO("SE", R.string.sweden, R.drawable.ic_flag_sweden, 46)),
        createCountryPair(CountryVO("UA", R.string.ukraine, R.drawable.ic_flag_ukraine, 380))
)

fun createCountryPair(countryVO: CountryVO): Pair<String, CountryVO> {
    return countryVO.regionCode to countryVO
}