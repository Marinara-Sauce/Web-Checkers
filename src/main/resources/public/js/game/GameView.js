/**
 * This module exports the GameView class constructor.
 * 
 * This component manages the Client-side behavior of the Game view.
 */
define(function(require){
  'use strict';
  
  // imports
  const BoardController = require('./BoardController');
  const GameState = require('./model/GameState');
  
  // MVP imports
  const PlayController = require('./modes/play/PlayController');

  // Project Enhancement imports
  const ReplayController = require('./modes/replay/ReplayController');
  const SpectatorController = require('./modes/spectator/SpectatorController');

  //
  // Constants
  //

  const PLAY_MODE = 'PLAY';
  const SPECTATOR_MODE = 'SPECTATOR';
  const REPLAY_MODE = 'REPLAY';

  //
  // Constructor
  //

  /**
   * Constructor function.
   */
  function GameView(gameState) {
    // private data
    this._gameState = gameState;
    this._boardController = new BoardController(this._gameState);
  }

  //
  // Public (external) methods
  //

  GameView.prototype.startup = function startup() {
    // initialize the Info fieldset with the player's names
    this.setRedPlayersName(this._gameState.getRedPlayer(), this._gameState.getRedRating());
    this.setWhitePlayersName(this._gameState.getWhitePlayer(), this._gameState.getWhiteRating());
    this.setTurnFlasher();
    
    // launch the Mode controller
    switch (this._gameState.getViewMode()) {
    case PLAY_MODE:
      console.debug('Play mode');
      this._modeController = new PlayController(this, this._boardController, this._gameState);
      break;
    case SPECTATOR_MODE:
      console.debug('Spectator mode');
      this._modeController = new SpectatorController(this, this._gameState);
      break;
    case REPLAY_MODE:
      console.debug('Replay mode');
      this._modeController = new ReplayController(this, this._gameState);
      break;
    default:
      alert('Unknown view module: ' + this._gameState.getViewMode());
      return;
    }
    this._modeController.startup();
  };

  GameView.prototype.setHelperText = function setHelperText(helpTextHTML) {
    jQuery("#help_text").html(helpTextHTML);
  };

  GameView.prototype.setRedPlayersName = function setRedPlayersName(name, rating) {
    jQuery("#game-info table[data-color='RED'] td.name").text(name);
    jQuery("#game-info table[data-color='RED'] td.rating").text("(" +rating+ ")");
    if(_gameState.getWinner() == _gameState.getRedPlayer()) {
        //default value for elo is 10 for now
        jQuery("#game-info table[data-color='RED'] td.win").text(" (+10)");
        jQuery("#game-info table[data-color='RED'] td.lose").text("");
    }
    else if(_gameState.getWinner() == _gameState.getWhitePlayer()) {
        jQuery("#game-info table[data-color='RED'] td.lose").text(" (-10)");
        jQuery("#game-info table[data-color='RED'] td.win").text("");
    }
  };

  GameView.prototype.setWhitePlayersName = function setWhitePlayersName(name, rating) {
    jQuery("#game-info table[data-color='WHITE'] td.name").text(name);
    jQuery("#game-info table[data-color='WHITE'] td.rating").text("(" +rating+ ")");
    if(this._gameState.getWinner() == this._gameState.getWhitePlayer()) {
        //default value for elo is 10 for now
        jQuery("#game-info table[data-color='WHITE'] td.win").text(" (+10)");
        jQuery("#game-info table[data-color='WHITE'] td.lose").text("");
    }
    else if(this._gameState.getWinner() == this._gameState.getRedPlayer()) {
        jQuery("#game-info table[data-color='WHITE'] td.lose").text(" (-10)");
        jQuery("#game-info table[data-color='WHITE'] td.win").text("");
    }
  };

  GameView.prototype.setTurnFlasher = function setTurnFlasher() {
    var activeColor = this._gameState.isRedsTurn() ? 'RED' : 'WHITE';
    var inactiveColor = this._gameState.isRedsTurn() ? 'WHITE' : 'RED';
    jQuery("#game-info table[data-color='" + activeColor + "']").addClass('isMyTurn');
    jQuery("#game-info table[data-color='" + inactiveColor + "']").removeClass('isMyTurn');
  };

  GameView.prototype.displayMessage = function displayMessage(message) {
    jQuery('#message').attr('class', message.type).html(message.text).slideDown(400);
  };

  /**
   * Queries whether the Game View can be deactivated; usually from
   * navigating way from the page.
   *
   * @return {boolean} true, if the user may navigate away from this page
   */
  GameView.prototype.canDeactivate = function canDeactivate() {
    return this._modeController.canDeactivate();
  };

  // export class constructor
  return GameView;
  
});

