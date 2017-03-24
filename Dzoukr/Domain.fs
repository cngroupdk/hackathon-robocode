namespace CN.Dzoukr.Robocode.Domain

open Robocode
open System

[<CLIMutable>]
type PositionOnField = {
    X : float
    Y : float
    FieldWidth : float
    FieldHeight : float
}
//with 
//    static member create(robot:Robot) = {
//        X = robot.X
//        Y = robot.Y
//        FieldWidth = robot.BattleFieldWidth
//        FieldHeight = robot.BattleFieldHeight
//    }

//type WallHitAlert =
//    | CloseToLeft
//    | CloseToRight
//    | CloseToTop
//    | CloseToBottom