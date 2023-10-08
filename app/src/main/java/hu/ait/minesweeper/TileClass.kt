package hu.ait.minesweeper

class TileClass (
    var hidden : Boolean = true,
    var flagged : Boolean = false,
    var bomb : Int = 0
){
    override fun toString(): String {
        return "TileClass { $bomb } hidden: $hidden   flagged:$flagged }"
    }
}