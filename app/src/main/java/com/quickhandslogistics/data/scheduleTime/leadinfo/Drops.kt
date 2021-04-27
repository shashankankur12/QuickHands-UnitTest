import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class Drops() : Parcelable{
	@SerializedName("count")
	@Expose
	var count: String? = null

	@SerializedName("time")
	@Expose
	var time: String? = null

	constructor(parcel: Parcel) : this() {
		count = parcel.readString()
		time = parcel.readString()
	}

	override fun writeToParcel(parcel: Parcel, flags: Int) {
		parcel.writeString(count)
		parcel.writeString(time)
	}

	override fun describeContents(): Int {
		return 0
	}

	companion object CREATOR : Parcelable.Creator<Drops> {
		override fun createFromParcel(parcel: Parcel): Drops {
			return Drops(parcel)
		}

		override fun newArray(size: Int): Array<Drops?> {
			return arrayOfNulls(size)
		}
	}
}