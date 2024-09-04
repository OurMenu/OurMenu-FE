package com.example.ourmenu.util

import android.util.Log
import com.example.ourmenu.R

object FolderIconUtil {

    fun indexToFolderResourceId(index: Any): Int {
        var newIndex = 0

        when (index) {
            is Int -> {
                newIndex = index
            }

            is String -> {
                try {
                    newIndex = index.toInt()
                } catch (e: Exception) {
                    Log.d("toIntError", e.toString())
                    return@indexToFolderResourceId R.drawable.ic_dice_2
                }
            }

            else -> {
                return@indexToFolderResourceId R.drawable.ic_dice_2
            }
        }

        return@indexToFolderResourceId when (newIndex) {
            0 -> R.drawable.ic_rec_tag_soban
            1 -> R.drawable.ic_rec_tag_jjajangmyeon
            2 -> R.drawable.ic_rec_tag_bread
            3 -> R.drawable.ic_rec_tag_rice
            4 -> R.drawable.ic_rec_tag_leaf
            5 -> R.drawable.ic_rec_tag_sushi
            6 -> R.drawable.ic_rec_tag_gibun
            7 -> R.drawable.ic_rec_tag_ramen
            8 -> R.drawable.ic_rec_tag_coffee
            9 -> R.drawable.ic_rec_tag_meat
            10 -> R.drawable.ic_rec_tag_fish
            11 -> R.drawable.ic_rec_tag_donut
            12 -> R.drawable.ic_rec_tag_hamburger
            13 -> R.drawable.ic_rec_tag_fire
            14 -> R.drawable.ic_rec_tag_ice_cream
            15 -> R.drawable.ic_rec_tag_samgyetang
            16 -> R.drawable.ic_rec_tag_soup_hot
            17 -> R.drawable.ic_rec_tag_alone
            18 -> R.drawable.ic_rec_tag_business
            19 -> R.drawable.ic_rec_tag_picnic
            20 -> R.drawable.ic_rec_tag_heart
            21 -> R.drawable.ic_rec_tag_group
            22 -> R.drawable.ic_rec_tag_spoon_chopstick
            23 -> R.drawable.ic_folder_chip_happy
            24 -> R.drawable.ic_folder_chip_sad
            25 -> R.drawable.ic_folder_chip_sun
            26 -> R.drawable.ic_folder_chip_cloudy
            27 -> R.drawable.ic_folder_chip_anger
            28 -> R.drawable.ic_folder_chip_rising_sun
            29 -> R.drawable.ic_folder_chip_snowman
            30 -> R.drawable.ic_folder_chip_boong_u_bbang
            else -> R.drawable.ic_dice_2
        }

    }
}
