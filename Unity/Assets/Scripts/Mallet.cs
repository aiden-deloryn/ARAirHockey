using UnityEngine;
using System.Collections;

public class Mallet : MonoBehaviour {
	float collisionForce = 20f;

	// Use this for initialization
	void Start () {
	
	}
	
	// Update is called once per frame
	void Update () {
	
	}

	void OnCollisionEnter(Collision other) {
		if(MultiTags.GameObjectHasTag(other.gameObject, Tag.Puck)) {
			other.gameObject.GetComponent<Rigidbody> ().AddForce ((other.gameObject.transform.position - this.gameObject.transform.position) * collisionForce);
		}
	}
}
