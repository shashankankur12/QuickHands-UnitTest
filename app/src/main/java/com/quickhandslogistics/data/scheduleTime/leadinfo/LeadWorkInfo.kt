import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LeadWorkInfo (

	@SerializedName("lead") val lead : String,
	@SerializedName("live") val live : Live,
	@SerializedName("drops") val drops : Drops,
	@SerializedName("outbounds") val outbounds : Outbounds,
	@SerializedName("shift") val shift : String,
	@SerializedName("department") val department : String,
	@SerializedName("totalContainers") val totalContainers : Int,
	@SerializedName("date") val date : String
) : Serializable