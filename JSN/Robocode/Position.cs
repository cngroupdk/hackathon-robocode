using System;
using System.Net;
using Robocode;

namespace Jsn
{
    public enum Quadrant
    {
        Nw,
        Ne,
        Sw,
        Se
    }
    class Position
    {
        public double X { get; set; }
        public double Y { get; set; }

        public Position() { }

        public Position(double x, double y)
        {
            Update(x, y);
        }

        public void Update(double x, double y)
        {
            X = x;
            Y = y;
        }

        public Position(Jsn myRobot, double bearing, double distance)
        {
            var angleRadians = Jsn.DegToRad(myRobot.Heading + bearing % 360);
            X = myRobot.X + Math.Sin(angleRadians) * distance;
            Y = myRobot.Y + Math.Cos(angleRadians) * distance;
        }

        public double GetDistance(Position that)
        {
            return Math.Sqrt(Math.Pow(Math.Abs(X - that.X), 2) + Math.Pow(Math.Abs(Y - that.Y), 2));
        }

        public Position Move(double heading, double velocity, long timeDelta)
        {
            var headingRadians = Jsn.DegToRad(heading);
            var distance = velocity * timeDelta;
            return new Position(X + Math.Sin(headingRadians) * distance, Y + Math.Cos(headingRadians) * distance);
        }

        public Quadrant GetQuadrant(double battlefieldHeight, double battlefieldWidth)
        {
            if (X > battlefieldWidth / 2)
            {
                if (Y > battlefieldHeight / 2)
                {
                    return Quadrant.Ne;
                }
                return Quadrant.Se;
            }

            if (Y > battlefieldHeight / 2)
            {
                return Quadrant.Nw;
            }
            return Quadrant.Sw;
        }

        public double GetBearing(Position that)
        {
            return Jsn.RadToDeg(Math.Acos(Math.Abs(that.Y - Y) / GetDistance(that)));
        }

        public double GetAbsoluteBearing(Position that)
        {
            double xo = that.X - X;
            double yo = that.Y - Y;
            double hyp = GetDistance(that);
            double arcSin = Jsn.RadToDeg(Math.Asin(xo / hyp));
            double bearing = 0;

            if (xo > 0 && yo > 0)
            { // both pos: lower-Left
                bearing = arcSin;
            }
            else if (xo < 0 && yo > 0)
            { // x neg, y pos: lower-right
                bearing = 360 + arcSin;
            }
            else if (xo > 0 && yo < 0)
            { // x pos, y neg: upper-left
                bearing = 180 - arcSin;
            }
            else if (xo < 0 && yo < 0)
            { // both neg: upper-right
                bearing = 180 - arcSin;
            }

            return bearing;
        }
    }
}
