# amsat-status

Submit and view satellite status on AMSAT

This project has two build flavors, dev for development and prod for production builds. Production
builds require an appropriate servies file to be generated from Firebase, but will be filtered out
if not present. Otherwise, they are equivalent to development flavors.

![Android workflow](https://github.com/penguin359/amsat-status/actions/workflows/android.yml/badge.svg)

If developing with Google Maps API support, please set MAPS_API_KEY in local.properties to your
Google Maps API key.
