package com.gdsc.fourcutalbum.data.model

data class FeedList(
    val feedList : ArrayList<Feed>,
    val page_number : Int,
    val hasNext : Boolean
)

// @SerializedName 어노테이션은 Kotlin 클래스의 필드 이름과 JSON 데이터의 필드 이름을 매핑시킵니다.
// JSON 데이터의 필드 이름과 Kotlin 클래스의 필드 이름이 다른 경우 이 어노테이션을 사용하여 매핑시켜줄 수 있습니다.

// @Expose 어노테이션은 해당 필드를 직렬화할지 여부를 지정합니다.
// 기본적으로 Gson 라이브러리에서는 모든 필드를 직렬화합니다.
// 따라서 @Expose 어노테이션을 사용하지 않아도 됩니다.
// 하지만 특정 필드를 직렬화하지 않고 무시하려는 경우에는 이 어노테이션을 사용할 수 있습니다.(@Expose(serialize = false))