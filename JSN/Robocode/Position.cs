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

        public Position() {}

        public Position(double x, double y)
        {
           Update(x, y);
        }

        public void Update(double x, double y)
        {
            X = x;
            Y = y;
        }

        public Position(Robot myRobot, double bearingRadians, double distance)
        {
            var absoluteBearing = (myRobot.Heading + bearingRadians) % 2 * Math.PI;
            X = myRobot.X + Math.Cos(absoluteBearing) * distance;
            Y = myRobot.Y + Math.Sin(absoluteBearing) * distance;
        }

        public double GetDistance(Position other)
        {
            return Math.Sqrt(Math.Pow(X + other.X, 2) + Math.Pow(Y + other.Y, 2));
        }

        public Position Move(double headingRadians, double velocity, long timeDelta)
        {
            var distance = velocity * timeDelta;
            return new Position(X = X + Math.Sin(headingRadians) * distance, Y + Math.Cos(headingRadians) * distance);
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
    }
}
