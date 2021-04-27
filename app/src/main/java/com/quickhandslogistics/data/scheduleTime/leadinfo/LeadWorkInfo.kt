import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.quickhandslogistics.data.dashboard.BuildingDetailData
import java.io.Serializable

class LeadWorkInfo(): Parcelable{

	@SerializedName("lead")
	@Expose
	var lead: String? = null

	@SerializedName("shift")
	@Expose
	var shift: String? = null

	@SerializedName("department")
	@Expose
	var department: String? = null

	@SerializedName("totalContainers")
	@Expose
	var totalContainers: Int? = null

	@SerializedName("date")
	@Expose
	var date: String? = null

	@SerializedName("live")
	@Expose
	var live: Outbounds? = null

	@SerializedName("drops")
	@Expose
	var drops: Drops? = null

	@SerializedName("outbounds")
	@Expose
	var outbounds: Outbounds? = null

	constructor(parcel: Parcel) : this() {
		lead = parcel.readString()
		shift = parcel.readString()
		department = parcel.readString()
		totalContainers = parcel.readValue(Int::class.java.classLoader) as? Int
		date = parcel.readString()
		live= parcel.readValue(Outbounds::class.java.classLoader) as Outbounds?
		outbounds= parcel.readValue(Outbounds::class.java.classLoader) as Outbounds?
		drops= parcel.readValue(Drops::class.java.classLoader) as Drops?
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(lead)
		parcel.writeString(shift)
		parcel.writeString(department)
		parcel.writeValue(totalContainers)
		parcel.writeString(date)
		parcel.writeValue(live)
		parcel.writeValue(outbounds)
		parcel.writeValue(drops)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<LeadWorkInfo> {
		override fun createFromParcel(parcel: Parcel): LeadWorkInfo {
			return LeadWorkInfo(parcel)
		}

		override fun newArray(size: Int): Array<LeadWorkInfo?> {
			return arrayOfNulls(size)
		}
	}

}
