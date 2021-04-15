import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Outbounds():Parcelable {
	@SerializedName("count")
	@Expose
	var count: String? = null

	@SerializedName("time")
	@Expose
	var time: List<String>? = null

	constructor(parcel: Parcel) : this() {
		count = parcel.readString()
		time = parcel.createStringArrayList()
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(count)
		parcel.writeStringList(time)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<Outbounds> {
		override fun createFromParcel(parcel: Parcel): Outbounds {
			return Outbounds(parcel)
		}

		override fun newArray(size: Int): Array<Outbounds?> {
			return arrayOfNulls(size)
		}
	}
}