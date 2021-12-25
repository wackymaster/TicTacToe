package model


class Move(private val value: Int, private val loc: Pair<Int, Int>) {

    fun getValue() : Int{
        return value
    }

    fun getLoc(): Pair<Int, Int> {
        return loc
    }

    override fun equals(other: Any?): Boolean {
        if(other !is Move) return false
        return other.value == this.value && other.loc == this.loc
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