# Shooting Stars
Provides a way to crowdsource shooting star information through an external server.
The following information is sent to the server when you have looked through a telescope:
* World
* Location of the star
* Minimum and maximum time for the star to land
* Keyword used to store your data

The code that the default server is based on can be found [here](https://github.com/andmcadams/shooting-stars-server). Note that it does not encrypt any of your data.

**The default endpoints have been changed as of 2026 to ones hosted by the starminers clan.** I do not own the infra that these endpoints are hosted on. They may break in the future, requiring you to use a different set of endpoints. Feel free to open any issues related to changing those here, or reach out to their clan if needed. In general, most people use one of several third party websites instead of this plugin to view found stars. This plugin is mostly used by those scouting stars.

## Why is there a warning when I download this plugin?
When installing this plugin, the following warning appears:

> This plugin submits the location and time of shooting stars along with your IP address to a 3rd party website not controlled or verified by the RuneLite Developers.

In order to use this plugin, you need to connect to a 3rd party (not RuneLite hosted or affiliated) server. Doing so has
some risks, though most people are probably worried specifically about the part about IP addresses.

The server side of this plugin (which holds and reports back all of your data) needs that IP address to know where to 
send the results back to. This is similar to how any website works. In addition, IPs may get saved in logs in order to
reduce spam and log errors. With the minimal amount of data I get, it would be very hard to associate an IP address with
a particular account/person. I will not use any logged IP addresses for nefarious purposes, but I cannot say the same
for any other people whose endpoints you connect to.

I will not change this warning's wording unless the RuneLite devs agree to whatever the new text is.
If you clone this repo and create a similar plugin, it is very likely
that you will need a similar warning, depending on their current policies.

If you do not feel comfortable with that, I highly recommend using one of the other shooting
star plugins that stores data locally or dump it for you to copy and paste.
If you're feeling really ambitious, you can always clone the server code and spin up your own server!
The use of an external server is not necessary, but it does make things more seamless.

## What is the shared key and how do I use it?
Using the same key as someone means you see everything they report and they see everything you report. By default,
everyone uses the `global` key. This is so the plugin works out of the box. However, it is not recommended to use this
key.

The key set in the plugin config must be 1-10 alpha characters (a-z, A-Z). You can share this key with others so you can see each
other's scouted stars.
Make sure to only share this with people you trust! Using a key with random people puts you at risk of them giving you fake reports.

As stated above, this key is not encrypted so do not use a real password.

## Rejected features
These are features that I will not merge, for various reasons. If you make a PR or issue with one of these, it will be
closed.

### Recording the exact location and tier of a star when you see it land
Any features that are meant to share the tier of a seen star or its exact location will be rejected. This feature was
previously considered, but it is very difficult to verify this kind of report and only encourages people to send fake
data to the plugin or turn the plugin off while mining stars. Anyway, this is tricky to display while keeping a clean
UI.

### Reducing restrictions on shared keys 
Shared keys are restricted in order to reduce the likelihood of someone using a real password.