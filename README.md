# Shooting Stars
Provides a way to crowdsource shooting star information through an external server.
The following information is sent to the server when you have looked through a telescope:
* World
* Location of the star
* Minimum and maximum time for the star to land
* Keyword used to store your data

The default server ([here](https://github.com/andmcadams/shooting-stars-server)) does not encrypt any of your data.

Please keep in mind that I work on this and host a default endpoint for fun. I have other obligations outside of this,
and while I do work on it from time to time, this is not what I spend all my time on. With that in mind, please be
patient with any PRs/issues that come up but if I haven't responded in a week or so, feel free to poke me again.

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

### Any changes that benefit a specific clan or clans in general
This plugin is not meant to help any clan or clans in general. If you want to make a plugin for a clan, you are more
than welcome to clone this repo and rebrand it with your endpoints. Clans are more than welcome to use this plugin,
as is anyone else, but they are not the target audience of this plugin.

### Any changes to accommodate an endpoint not hosted by me
This includes anything from sending extra info to the endpoints to changing the default endpoint. I don't have control
over other people's servers, and I have no idea what they are using the extra info for. Of course, you are always welcome
to clone this repo and make changes to it for your use case. That way users can download your plugin separately and know
what info they are passing off to you.

### Reducing restrictions on shared keys 
Shared keys are restricted in order to reduce the likelihood of someone using a real password.