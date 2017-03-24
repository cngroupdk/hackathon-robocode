module CN.Dzoukr.Robocode.Core

open Robocode
open System

type Setup = {
    Color : System.Drawing.Color
    WallLimit : float
}

type PositionOnField = {
    X : float
    Y : float
    FieldWidth : float
    FieldHeight : float
}
with 
    static member create(robot:Robot) = {
        X = robot.X
        Y = robot.Y
        FieldWidth = robot.BattleFieldWidth
        FieldHeight = robot.BattleFieldHeight
    }

type WallHitAlert =
    | CloseToLeft
    | CloseToRight
    | CloseToTop
    | CloseToBottom

//let isHeadingLeft heading = heading >= 225. && heading <= 315.
//let isHeadingRight heading = heading >= 45. && heading <= 135.
//let isHeadingTop heading = heading >= 315. || heading <= 45.
//let isHeadingDown heading = heading >= 135. || heading <= 225.

let (|WallHitAlert|_|) (config, position, heading) =
    if position.X <= config.WallLimit then //&& isHeadingLeft heading then
        Some CloseToLeft
    else if position.X >= position.FieldWidth - config.WallLimit then // && isHeadingRight heading then
        Some CloseToRight
    else if position.Y <= config.WallLimit then //&& isHeadingDown heading then
        Some CloseToBottom
    else if position.Y >= position.FieldHeight - config.WallLimit then //&& isHeadingTop heading then
        Some CloseToTop
    else None

//type Movement =
//    | Escape
//    | Attack
//    | Search
//    | AvoidWall of WallHitAlert

type Action =
    | Move of degrees:float * distance:float
    | Fire of degrees:float * power:float
    | Scan of degrees:float

type Mode =
    | Search
    | Attack
    | RunAway

let randomWithCenter center =
    let center = int center
    let min,max = (center - 22),(center + 22)
    System.Random().Next(min, max) |> float

let calculateNextAction (robot:Robot) config  =
    let position = PositionOnField.create(robot)
    let moveAway = config.WallLimit * 1.5
    match config, position, robot.Heading with
    | WallHitAlert(CloseToLeft) -> Move(randomWithCenter 90., moveAway)
    | WallHitAlert(CloseToRight) -> Move(randomWithCenter 270., moveAway)
    | WallHitAlert(CloseToTop) -> Move(randomWithCenter 180., moveAway)
    | WallHitAlert(CloseToBottom) -> Move(randomWithCenter 0., moveAway)
    | _ -> Move(robot.Heading, 100.)

let calculateScannedBotActions (robot:Robot) (args:ScannedRobotEvent)  =
    let heading = robot.Heading + args.Bearing
    let firePower = 400. / args.Distance
    [
        Fire(heading, firePower)
        Move(heading, 50.)
        Scan(heading)
    ]

let effectiveTurn (robot:Robot) degrees =
    match robot.Heading, degrees with
    | h,d when h = d -> ()
    | h,d ->
        let abs = Math.Abs(h-d)
        if abs >= 180. then
            robot.TurnRight abs
        else robot.TurnLeft abs

let effectiveRadarTurn (robot:Robot) degrees =
    match robot.RadarHeading, degrees with
    | h,d when h = d -> ()
    | h,d ->
        let abs = Math.Abs(h-d)
        if abs >= 180. then
            robot.TurnRadarRight abs
        else robot.TurnRadarLeft abs


let applyAction (robot:Robot) = function
    | Move(degrees, distance) -> 
        degrees |> effectiveTurn robot
        robot.Ahead(distance)
    | Fire(degrees, power) ->
        degrees |> effectiveTurn robot
        robot.Fire(power)
    | Scan(degrees) ->
        degrees |> effectiveRadarTurn robot

let applyActions (robot:Robot) = List.iter (applyAction robot)