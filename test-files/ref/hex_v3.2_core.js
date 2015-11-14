/// COPYRIGHT 2013 GEOHEX Inc. ///
/// GEOHEX by @sa2da (http://geohex.net) is licensed under Creative Commons BY-SA 2.1 Japan License. ///

///////////[2010.12.14 v3公開]/////////
///////////[2010.12.28 zoneByCode()内のlonを±180以内に補正]/////////
///////////[2011.9.11 zoneByCode()内のh_x,h_yを補正]/////////
///////////[2013.1.6 180度線をまたぐ場合のHEXのX,Y値を一意に補正]/////////
///////////[2013.1.6 範囲外のX,Y値を範囲内に補正する関数 adjustXY() を追加]/////////
///////////[2013.1.6 X,Y値からHEXを取得する関数 getZoneByXY() を追加]/////////
///////////[2013.1.6 NamespaceをGeoHexからGEOHEXに変更]/////////
///////////[2013.1.9 adjustXYの補正ロジックを修正]/////////
///////////[2013.1.11 getZoneByLocation()→getXYByLocation()とgetZoneByXY()に分割]/////////
///////////[2013.1.11 getZoneByCode()→getXYByCode()とgetZoneByXY()に分割]/////////
///////////[2013.1.11 adjustXY補正のx<yをx>yの場合に-180補正へ修正]/////////
///////////[2013.1.17 getZoneByXY()内 -180度補正Code生成ロジックを修正]/////////

(function (win) {

// namspace GEOHEX;
if (!win.GEOHEX)	win.GEOHEX = function(){};
// version: 3.2
GEOHEX.version = "3.2";
GEOHEX.cache_on = true;

// *** Share with all instances ***
var h_key = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
var h_base = 20037508.34;
var h_deg = Math.PI*(30/180);
var h_k = Math.tan(h_deg);

// private static
var _zoneCache = {};

// *** Share with all instances ***
// private static
function calcHexSize(_level) {
	return h_base/Math.pow(3, _level+3);
}

// private class
function Zone(lat, lon, x, y, code) {
	this.lat = lat;
	this.lon = lon;
	this.x = x;
	this.y = y;
	this.code = code;
}
Zone.prototype.getLevel = function () {
	return this.code.length-2;
};
Zone.prototype.getHexSize = function () {
	return calcHexSize(this.getLevel());
};
Zone.prototype.getHexCoords = function () {
	var h_lat = this.lat;
	var h_lon = this.lon;
	var h_xy = loc2xy(h_lon, h_lat);
	var h_x = h_xy.x;
	var h_y = h_xy.y;
	var h_deg = Math.tan(Math.PI * (60 / 180));
	var h_size = this.getHexSize();
	var h_top = xy2loc(h_x, h_y + h_deg *  h_size).lat;
	var h_btm = xy2loc(h_x, h_y - h_deg *  h_size).lat;

	var h_l = xy2loc(h_x - 2 * h_size, h_y).lon;
	var h_r = xy2loc(h_x + 2 * h_size, h_y).lon;
	var h_cl = xy2loc(h_x - 1 * h_size, h_y).lon;
	var h_cr = xy2loc(h_x + 1 * h_size, h_y).lon;
	return [
		{lat: h_lat, lon: h_l},
		{lat: h_top, lon: h_cl},
		{lat: h_top, lon: h_cr},
		{lat: h_lat, lon: h_r},
		{lat: h_btm, lon: h_cr},
		{lat: h_btm, lon: h_cl}
	];
};


function getZoneByLocation(_lat, _lon, _level) {
	var xy = getXYByLocation(_lat, _lon, _level);
	var zone = getZoneByXY(xy.x, xy.y, _level);
	return zone;
}

function getZoneByCode(_code) {
	var xy = getXYByCode(_code);
	var level = _code.length - 2;
	var zone = getZoneByXY(xy.x, xy.y, level);
	return zone;
}

function getXYByLocation(lat, lon, _level) {
	var h_size = calcHexSize(_level);
	var z_xy = loc2xy(lon, lat);
	var lon_grid = z_xy.x;
	var lat_grid = z_xy.y;
	var unit_x = 6 * h_size;
	var unit_y = 6 * h_size * h_k;
	var h_pos_x = (lon_grid + lat_grid / h_k) / unit_x;
	var h_pos_y = (lat_grid - h_k * lon_grid) / unit_y;
	var h_x_0 = Math.floor(h_pos_x);
	var h_y_0 = Math.floor(h_pos_y);
	var h_x_q = h_pos_x - h_x_0; 
	var h_y_q = h_pos_y - h_y_0;
	var h_x = Math.round(h_pos_x);
	var h_y = Math.round(h_pos_y);
	
	if (h_y_q > -h_x_q + 1) {
		if((h_y_q < 2 * h_x_q) && (h_y_q > 0.5 * h_x_q)){
			h_x = h_x_0 + 1;
			h_y = h_y_0 + 1;
		}
	} else if (h_y_q < -h_x_q + 1) {
		if ((h_y_q > (2 * h_x_q) - 1) && (h_y_q < (0.5 * h_x_q) + 0.5)){
			h_x = h_x_0;
			h_y = h_y_0;
		}
	}
	
	var inner_xy = adjustXY(h_x,h_y,_level);
	h_x = inner_xy.x;
	h_y = inner_xy.y;
	return {"x": h_x, "y":h_y};
}


function getXYByCode(code) {
	var level = code.length -2;
	var h_size =  calcHexSize(level);
	var unit_x = 6 * h_size;
	var unit_y = 6 * h_size * h_k;
	var h_x = 0;
	var h_y = 0;
	var h_dec9 =""+ (h_key.indexOf(code.charAt(0))*30+h_key.indexOf(code.charAt(1)))+code.substring(2);
	if(h_dec9.charAt(0).match(/[15]/)&&h_dec9.charAt(1).match(/[^125]/)&&h_dec9.charAt(2).match(/[^125]/)){
	  if(h_dec9.charAt(0)==5){
		h_dec9 = "7"+h_dec9.substring(1,h_dec9.length);
	  }else if(h_dec9.charAt(0)==1){
		h_dec9 = "3"+h_dec9.substring(1,h_dec9.length);
	  }
	}
	var d9xlen = h_dec9.length;
	for(i=0;i<level +3 - d9xlen;i++){
	  h_dec9 ="0"+h_dec9;
	  d9xlen++;
	}
	var h_dec3 = "";
	for(i=0;i<d9xlen;i++){
	  var h_dec0=parseInt(h_dec9.charAt(i)).toString(3);
	  if(!h_dec0){
	    h_dec3 += "00";
	  }else if(h_dec0.length==1){
	    h_dec3 += "0";
	  }
	  h_dec3 += h_dec0;
	}

	h_decx =new Array();
	h_decy =new Array();
	
	for(i=0;i<h_dec3.length/2;i++){
	  h_decx[i]=h_dec3.charAt(i*2);
	  h_decy[i]=h_dec3.charAt(i*2+1);
	}

	for(i=0;i<=level+2;i++){
	    var h_pow = Math.pow(3,level+2-i);
	    if(h_decx[i] == 0){
	        h_x -= h_pow;
	    }else if(h_decx[i] == 2){
	        h_x += h_pow;
	    }
	    if(h_decy[i] == 0){
	        h_y -= h_pow;
	    }else if(h_decy[i] == 2){
	        h_y += h_pow;
	    }
	}
	
	var inner_xy = adjustXY(h_x,h_y,level);
	h_x = inner_xy.x;
	h_y = inner_xy.y;
	
	return {"x":h_x, "y":h_y};
}

function getZoneByXY(_x, _y, _level) {
	var h_size = calcHexSize(_level);
	
	var h_x =_x;
	var h_y=_y;

	var unit_x = 6 * h_size;
	var unit_y = 6 * h_size * h_k;

	var h_lat = (h_k * h_x * unit_x + h_y * unit_y) / 2;
	var h_lon = (h_lat - h_y * unit_y) / h_k;

	var z_loc = xy2loc(h_lon, h_lat);
	var z_loc_x = z_loc.lon;
	var z_loc_y = z_loc.lat;
	
	var max_hsteps = Math.pow(3,_level+2);
	var hsteps = Math.abs(h_x - h_y);
	
	if(hsteps==max_hsteps){
		if(h_x>h_y){
		var tmp = h_x;
		h_x = h_y;
		h_y = tmp;
		}
		z_loc_x = -180;
	}
	
	var h_code ="";
	var code3_x =new Array();
	var code3_y =new Array();
	var code3 ="";
	var code9="";
	var mod_x = h_x;
	var mod_y = h_y;


	for(i = 0;i <= _level+2 ; i++){
	  var h_pow = Math.pow(3,_level+2-i);
	  if(mod_x >= Math.ceil(h_pow/2)){
	    code3_x[i] =2;
	    mod_x -= h_pow;
	  }else if(mod_x <= -Math.ceil(h_pow/2)){
	    code3_x[i] =0;
	    mod_x += h_pow;
	  }else{
	    code3_x[i] =1;
	  }
	  if(mod_y >= Math.ceil(h_pow/2)){
	    code3_y[i] =2;
	    mod_y -= h_pow;
	  }else if(mod_y <= -Math.ceil(h_pow/2)){
	    code3_y[i] =0;
	    mod_y += h_pow;
	  }else{
	    code3_y[i] =1;
	  }
	  if(i==2&&(z_loc_x==-180 || z_loc_x>=0)){
		  if(code3_x[0]==2&&code3_y[0]==1&&code3_x[1]==code3_y[1]&&code3_x[2]==code3_y[2]){
			code3_x[0]=1;
			code3_y[0]=2;
		  }else if(code3_x[0]==1&&code3_y[0]==0&&code3_x[1]==code3_y[1]&&code3_x[2]==code3_y[2]){
			code3_x[0]=0;
			code3_y[0]=1
		  }
	  }
	}

	for(i=0;i<code3_x.length;i++){
	  code3 += ("" + code3_x[i] + code3_y[i]);
	  code9 += parseInt(code3,3);
	  h_code += code9;
	  code9="";
	  code3="";
	}
	var h_2 = h_code.substring(3);
	var h_1 = h_code.substring(0,3);
	var h_a1 = Math.floor(h_1/30);
	var h_a2 = h_1%30;
	h_code = (h_key.charAt(h_a1)+h_key.charAt(h_a2)) + h_2;

	if(GEOHEX.cache_on){
		if (!!_zoneCache[h_code])	return _zoneCache[h_code];
		return (_zoneCache[h_code] = new Zone(z_loc_y, z_loc_x, _x, _y, h_code));
	}else{
		return new Zone(z_loc_y, z_loc_x, _x, _y, h_code);
	}
}

function adjustXY(_x, _y, _level){
	var x =_x;
	var y =_y;
	var rev = 0;
	var max_hsteps = Math.pow(3,_level+2);
	var hsteps = Math.abs(x - y);
	if(hsteps==max_hsteps&&x>y){
		var tmp = x;
		x = y;
		y = tmp;
		rev =1;
	}else if(hsteps>max_hsteps){
		var dif = hsteps - max_hsteps;
		var dif_x = Math.floor(dif/2);
		var dif_y = dif - dif_x;
		var edge_x;
		var edge_y;
		if(x>y){
			edge_x = x - dif_x;
			edge_y = y + dif_y;
			var h_xy = edge_x;
			edge_x = edge_y;
			edge_y = h_xy;
			x = edge_x + dif_x;
			y = edge_y - dif_y;
		}else if(y>x){
			edge_x = x + dif_x;
			edge_y = y - dif_y;
			var h_xy = edge_x;
			edge_x = edge_y;
			edge_y = h_xy;
			x = edge_x - dif_x;
			y = edge_y + dif_y;
		}
	}
	return { x: x, y: y , rev:rev};
}

// private static
function loc2xy(lon, lat) {
	var x = lon * h_base / 180;
	var y = Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180);
	y *= h_base / 180;
	return { x: x, y: y };
}
// private static
function xy2loc(x, y) {
	var lon = (x / h_base) * 180;
	var lat = (y / h_base) * 180;
	lat = 180 / Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180)) - Math.PI / 2);
	return { lon: lon, lat: lat };
}

// EXPORT
GEOHEX.getZoneByLocation = getZoneByLocation;
GEOHEX.getXYByLocation = getXYByLocation;
GEOHEX.getZoneByCode = getZoneByCode;
GEOHEX.getZoneByXY = getZoneByXY;
GEOHEX.loc2xy = loc2xy;
GEOHEX.xy2loc = xy2loc;
GEOHEX.adjustXY = adjustXY;

})(this);