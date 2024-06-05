package com.example.storyappfix

import com.example.storyappfix.entity.StoryEntity

object DataDummy {

    fun generateDummyQuoteResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..100) {
            val story = StoryEntity(
                i.toString(),
                "createdAt + $i",
                "name $i",
                "description $i",
                -7.880855,
                "id",
                110.400545
            )
            items.add(story)
        }
        return items
    }
}