import com.google.gson.annotations.SerializedName

data class Outbounds (

	@SerializedName("count") val count : Int,
	@SerializedName("time") val time : List<String>
)