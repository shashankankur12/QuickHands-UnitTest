import com.google.gson.annotations.SerializedName

data class Drops (

	@SerializedName("count") val count : Int,
	@SerializedName("time") val time : String
)