import com.google.gson.annotations.SerializedName

data class Live (

	@SerializedName("count") val count : Int,
	@SerializedName("time") val time : List<String>
)