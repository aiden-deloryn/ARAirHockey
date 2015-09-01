﻿using UnityEngine;
using System.Collections;

public class FollowMouse : MonoBehaviour {

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
		Vector3 mouseScreenPosition = Input.mousePosition;
		Vector3 mousePosition = Camera.main.ScreenToWorldPoint(mouseScreenPosition);
		Vector3 targetPosition = new Vector3(mousePosition.x, this.gameObject.transform.position.y, mousePosition.z);

//		this.gameObject.transform.position = targetPosition;
		this.gameObject.GetComponent<Rigidbody> ().MovePosition (targetPosition);
	}

	void OnCollisionEnter(Collision other) {
		Debug.Log ("Collision!");
		//other.gameObject.GetComponent<Rigidbody> ().AddForce (Vector3.forward);
		other.gameObject.GetComponent<Rigidbody> ().AddForce ((other.gameObject.transform.position - this.gameObject.transform.position) * 5);
	}
}
