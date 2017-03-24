using System;
using System.Collections.Generic;
using Robocode;
using System.Drawing;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Security.Cryptography.X509Certificates;

namespace Jsn
{
    public class Jsn : Robot
    {
        public const double WallMargin = 200;
        private Dictionary<string, Target> _targets = new Dictionary<string, Target>();
        private Position _position = new Position();
        private double _desiredHeading = 0;
        private int _strafeDirection = 1;
        private Target _lockedTarget;
        private double _desiredGunHeading;

        public override void Run()
        {
            IsAdjustGunForRobotTurn = false;
            IsAdjustRadarForGunTurn = false;
            IsAdjustRadarForRobotTurn = false;
            SetAllColors(Color.Orange);

            while (true)
            {
                _position.Update(X, Y);
                // scan surroundings
                TurnRadarRight(360);
                _lockedTarget = GetClosestTarget();

                // move out of way of bullets

                // base of number of oponents choose movement strategy
                if (Others > 3)
                {
                    // edges
                    if (AmICloseToWall())
                    {
                        var quadrant = _position.GetQuadrant(BattleFieldHeight, BattleFieldWidth);
                        switch (quadrant)
                        {
                            case Quadrant.Ne:
                                _desiredHeading = 270;
                                break;
                            case Quadrant.Nw:
                                _desiredHeading = 180;
                                break;
                            case Quadrant.Se:
                                _desiredHeading = 0;
                                break;
                            case Quadrant.Sw:
                                _desiredHeading = 90;
                                break;
                        }
                    }

                    TurnRight(NormalizeBearing(_desiredHeading - Heading));
                    Ahead(185);
                }
                else
                {
                    // strafe closing in
                    _desiredHeading = (Heading - RadarHeading + _lockedTarget.Bearing + 90 - 15);
                    TurnRight(NormalizeBearing(_desiredHeading * _strafeDirection));
                    Ahead(GetRandomNumber(50, 120));
                    _strafeDirection *= -1;
                }

                _desiredGunHeading = NormalizeBearing(Heading - RadarHeading + _lockedTarget.Bearing);
                TurnGunRight(_desiredHeading);

                if (GunHeat < 0.1 && Math.Abs(GunHeading -_desiredHeading) < 1)
                {
                    var firePower = Math.Min(500 / _lockedTarget.Distance, 3);
                    var bulletSpeed = 20 - firePower * 3;
                    var time = (long)(_lockedTarget.Distance / bulletSpeed);
                    Fire(firePower);
                }

            }
        }

        public double GetRandomNumber(double minimum, double maximum)
        {
            Random random = new Random();
            return random.NextDouble() * (maximum - minimum) + minimum;
        }

        public double NormalizeBearing(double bearing)
        {
            var normalizedBearing = bearing;
            while (normalizedBearing > 180)
            {
                normalizedBearing -= 360;
            }
            while (normalizedBearing < -180)
            {
                normalizedBearing += 360;
            }
            return normalizedBearing;
        }

        private void CleanupTargets()
        {
            // remove targets not scanned in last couple turns
            // remap targets which returned to field of view by name if possible
        }

        private bool AmICloseToWall()
        {
            return (_position.X < WallMargin ||
                _position.X > (BattleFieldWidth - WallMargin) ||
                _position.Y < WallMargin ||
                _position.Y > (BattleFieldHeight - WallMargin));
        }


        public override void OnScannedRobot(ScannedRobotEvent e)
        {
            if (_targets.ContainsKey(e.Name))
            {
                _targets[e.Name].Update(e, this);
            }
            else
            {
                _targets.Add(e.Name, new Target(e, this));
            }

            // check if shot
        }

        public override void OnRobotDeath(RobotDeathEvent e)
        {
            // remove from targets
            if (_targets.ContainsKey(e.Name)) _targets.Remove(e.Name);
        }

        private Target GetClosestTarget()
        {
            return _targets.First(a => a.Value.Distance == _targets.Values.Min(t => t.Distance)).Value;
        }


    }
}
