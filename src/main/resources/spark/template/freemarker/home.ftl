<!DOCTYPE html>

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
  <meta http-equiv="refresh" content="10">
  <title>Web Checkers | ${title}</title>
  <link rel="stylesheet" type="text/css" href="/css/style.css">
  <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
  <script>
  $(document).ready(function() {
    $(".not-playable").click(function(){
        yes = window.confirm("This player is currently in a game. Do you want to spectate them?");
        if(!yes){
            $(".players").submit(function(e) {
                e.preventDefault();
                location.reload();
            });
        }
    });
  });
  </script>
</head>

<body>
<div class="page">

  <h1>Web Checkers | ${title}</h1>

  <!-- Provide a navigation bar -->
  <#include "nav-bar.ftl" />

  <div class="body">

    <!-- Provide a message to the user, if supplied. -->
    <#include "message.ftl" />

    <!-- Display current players online -->
    <#if numPlayers??>
      Current Players Online: ${numPlayers}
    </#if>

    <!-- Display list of joinable players -->
    <#if players??>
    Your ELO Rating: <#include "rating.ftl" /><br>
      <#if players?size gte 1>
        Signed in Players:<br/><br/>
        <#list players as player>
          <!-- ${player} -->
          <form class="players" method="POST" action="/selectuser">
          <#if player.spectating>
            <button type="submit" name="opponent" value="${player.name}" class="spectating"> ${player.name} <#include "rating.ftl" /> </button>
          <#elseif !player.playable>
            <button type="submit" name="opponent" value="${player.name}" class="not-playable"> ${player.name} <#include "rating.ftl" /> </button>
          <#else>
            <button type="submit" name="opponent" value="${player.name}" class="player"> ${player.name} <#include "rating.ftl" /> </button>
          </#if>
          </form>
          <br/>
        </#list>
      <#else>
        No other players are signed in
      </#if>
    </#if>
  </div>
</div>
</body>

</html>