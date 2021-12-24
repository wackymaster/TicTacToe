package model


class Move(private val value: Int, private val loc: Pair<Int, Int>) {

    fun getValue() : Int{
        return value
    }

    fun getLoc(): Pair<Int, Int> {
        return loc
    }

    override fun equals(other: Any?): Boolean {
        return other.hashCode() == this.hashCode()
    }
    
    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + loc.hashCode()
        return result
    }

    override fun toString(): String {
        return "value: $value to $loc"
    }
}